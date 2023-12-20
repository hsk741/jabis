package com.jabis.refund.service;

import com.jabis.refund.core.security.cipher.CryptographyOperations;
import com.jabis.refund.core.security.cipher.OneWayPasswordHashOperations;
import com.jabis.refund.core.security.token.OAuth2Token;
import com.jabis.refund.core.security.token.OAuth2TokenConverter;
import com.jabis.refund.dto.*;
import com.jabis.refund.dto.scrap.*;
import com.jabis.refund.exception.*;
import com.jabis.refund.repository.*;
import com.jabis.refund.repository.entity.scrap.Deduction;
import com.jabis.refund.repository.entity.scrap.Salary;
import com.jabis.refund.repository.entity.user.UserProfile;
import com.jabis.refund.rule.RefundPolicy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserProfileRepository userProfileRepository;

    private final SalaryRepository salaryRepository;

    private final DeductionRepository deductionRepository;

    private final OneWayPasswordHashOperations hashOperator;

    private final CryptographyOperations cryptoOperator;

    private final OAuth2TokenConverter oAuth2TokenConverter;

    private final ScrapRefundCollectorRepository scrapRefundCollectorRepository;
    private final EnableUserProfileRepository enableUserProfileRepository;

    public UserService(UserProfileRepository userProfileRepository,
                       SalaryRepository salaryRepository,
                       DeductionRepository deductionRepository,
                       OneWayPasswordHashOperations hashOperator,
                       CryptographyOperations cryptoOperator,
                       OAuth2TokenConverter oAuth2TokenConverter,
                       ScrapRefundCollectorRepository scrapRefundCollectorRepository,
                       EnableUserProfileRepository enableUserProfileRepository) {

        this.userProfileRepository = userProfileRepository;
        this.salaryRepository = salaryRepository;
        this.deductionRepository = deductionRepository;
        this.hashOperator = hashOperator;
        this.cryptoOperator = cryptoOperator;
        this.oAuth2TokenConverter = oAuth2TokenConverter;
        this.scrapRefundCollectorRepository = scrapRefundCollectorRepository;
        this.enableUserProfileRepository = enableUserProfileRepository;
    }

    @Transactional
    public UserProfileResponse createUser(UserProfileDto userProfileDto) {

        final String encryptedRegNo = cryptoOperator.encrypt(userProfileDto.getRegNo());

//      가입 가능한 유저 조회
        enableUserProfileRepository.findByNameAndRegNo(userProfileDto.getName(), encryptedRegNo)
                                   .orElseThrow(NotEnabledUserException::new);

//      기가입자 조회
        userProfileRepository.findByRegNo(encryptedRegNo)
                             .ifPresent(user -> { throw new AlreadyExistedUserException(); });

        final UserProfileDto cryptedPasswordUserProfileDto = userProfileDto.toBuilder()
                                                                           .password(hashOperator.hash(userProfileDto.getPassword()))
                                                                           .regNo(encryptedRegNo)
                                                                           .build();

        final UserProfile savedUserProfile = userProfileRepository.save(new UserProfile(cryptedPasswordUserProfileDto));

        return new UserProfileResponse(savedUserProfile);
    }

    public OAuth2Token login(UserIdPasswordToken userIdPasswordToken) {

        final UserProfile userProfile = userProfileRepository.findByUserId(userIdPasswordToken.getUserId())
                                                             .orElseThrow(NotFoundUserException::new);

        final boolean isMatched = hashOperator.verify(userIdPasswordToken.getPassword(), userProfile.getPassword());

        if (isMatched) {
            return oAuth2TokenConverter.encode(userProfile.getUserId());
        }

        throw new NotMatchedPasswordException();
    }

    public UserProfileDto getMyProfile(String userId) {

        final UserProfile userProfile = userProfileRepository.findByUserId(userId).orElseThrow(NotFoundUserException::new);
        final UserProfileDto userProfileDto = new UserProfileDto(userProfile);

        return userProfileDto.toBuilder().regNo(cryptoOperator.decrypt(userProfileDto.getRegNo())).build();
    }

    @Transactional
    public JsonList scrap(String userId) {

        final UserProfile userProfile = userProfileRepository.findByUserId(userId).orElseThrow(NotFoundUserException::new);

        final ScrapResponse scrapResponseBody = scrapRefundCollectorRepository.getScrapResponse(userProfile.getName(),
                                                                                                cryptoOperator.decrypt(userProfile.getRegNo()));

        final JsonList jsonList;

        if (scrapResponseBody != null && scrapResponseBody.getStatus().equalsIgnoreCase("success")) {

            salaryRepository.deleteByUserId(userId);
            deductionRepository.deleteByUserId(userId);

            final Data data = scrapResponseBody.getData();
            jsonList = data.getJsonList();

            // 소득공제
            final Deduction deduction = Deduction.builder()
                                                 .userId(userId)        // 사용자 ID
                                                 .calculatedTaxAmount(getValueWithoutComma(jsonList.getCaculatedTaxAmount()))   // 산출세액
                                                 .build();
            final List<DeductionItem> deductionItems = jsonList.getDeductionItems();
            deductionRepository.saveAll(deductionItems.stream()
                                                      .map(deductionItem -> deduction.setAmountAboutClassification(deductionItem.getAmount(), deductionItem.getIncomeClassification()))
                                                      .toList());

            // 급여
            final List<SalaryItem> salaryItems = jsonList.getSalaryItems();
            salaryRepository.saveAll(salaryItems.stream()
                                                .map(salaryItem -> new Salary(userId, salaryItem))
                                                .map(salary -> salary.setDeduction(deduction))  // 연관관계 설정
                                                .toList());
        } else {
            throw new ScrapFailureException("response status failure");
        }

        return jsonList;
    }

    public ScrapRefundResponse getRefund(String userId) {

        final Deduction deduction = deductionRepository.findByUserId(userId)
                                                       .orElseThrow(() -> new NotFoundScrapingDataException("Not found deduction!!!"));

        if(CollectionUtils.isEmpty(deduction.getSalaries()))
            throw new NotFoundScrapingDataException("Not found salary!!!");

        final UserProfile userProfile = userProfileRepository.findByUserId(userId).orElseThrow(NotFoundUserException::new);

//      결정세액
        final DeterminedTaxAmount determinedTaxAmount = RefundPolicy.getDeterminedTaxAmount(
                deduction.getCalculatedTaxAmount(),  // 산출세액
                deduction.getSalaries().stream().mapToDouble(Salary::getTotalPaidAmount).sum(),    // 총급여
                deduction.getRetirementPension(),       // 퇴직연금
                deduction.getInsuranceFee(),            // 보험료
                deduction.getMedicalExpenses(),         // 의료비
                deduction.getEducationExpenses(),       // 교육비
                deduction.getDonation());               // 기부금

        return new ScrapRefundResponse(userProfile.getName(), Math.round(determinedTaxAmount.determinedTaxAmount()), Math.round(determinedTaxAmount.retirementPensionTaxCreditAmount()));
    }

    private double getValueWithoutComma(String value) {

        NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
        final Number number;
        try {
            number = format.parse(value);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return number.doubleValue();
    }
}
