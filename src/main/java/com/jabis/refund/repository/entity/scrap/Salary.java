package com.jabis.refund.repository.entity.scrap;

import com.jabis.refund.dto.scrap.SalaryItem;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@AllArgsConstructor
public class Salary implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String userId;

    @Column
    private String incomeDetails;

    @Column
    private Double totalPaidAmount;

    @Column
    private LocalDate bizStartDate;

    @Column
    private String companyName;

    @Column
    private String name;

    @Column
    private LocalDate paymentDate;

    @Column
    private LocalDate bizEndDate;

    @Column
    private String residentRegistrationNumber;

    @Column
    private String incomeClassification;

    @Column
    private String companyRegistrationNumber;

    @CreatedDate
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deduction_id")
    private Deduction deduction;

    public Salary(String userId, SalaryItem salaryItem) {

        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

        this.userId = userId;
        this.incomeDetails = salaryItem.getIncomeDetails();
        this.totalPaidAmount = getValueWithoutComma(salaryItem.getTotalPaidAmount());
        this.bizStartDate = LocalDate.parse(salaryItem.getBizStartDate(), dateTimeFormatter);
        this.companyName = salaryItem.getCompanyName();
        this.name = salaryItem.getName();
        this.paymentDate = LocalDate.parse(salaryItem.getPaymentDate(), dateTimeFormatter);
        this.bizEndDate = LocalDate.parse(salaryItem.getBizEndDate(), dateTimeFormatter);
        this.residentRegistrationNumber = salaryItem.getResidentRegistrationNumber();
        this.incomeClassification = salaryItem.getIncomeClassification();
        this.companyRegistrationNumber = salaryItem.getCompanyRegistrationNumber();
    }

    public Salary setDeduction(Deduction deduction) {
        this.deduction = deduction;
        deduction.getSalaries().add(this);
        return this;
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
