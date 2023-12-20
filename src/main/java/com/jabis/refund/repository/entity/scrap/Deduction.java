package com.jabis.refund.repository.entity.scrap;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@AllArgsConstructor
public class Deduction implements Serializable {

	@Id
	@GeneratedValue
	private Long id;

	@Column
	private String userId;

	// 보험료
	@Column
	private Double insuranceFee;

	// 교육비
	@Column
	private Double educationExpenses;

	// 기부금
	@Column
	private Double donation;

	// 의료비
	@Column
	private Double medicalExpenses;

	// 퇴직연금
	@Column
	private Double retirementPension;

	@Column
	private Double calculatedTaxAmount;

	@CreatedDate
	private LocalDateTime createdAt;

	@OneToMany(mappedBy = "deduction")
	@Builder.Default
	private List<Salary> salaries = new ArrayList<>();

	public Deduction setAmountAboutClassification(String amount, String incomeClassification) {

		final double valueWithoutComma = getValueWithoutComma(amount);

        switch (incomeClassification) {
            case "보험료" -> this.insuranceFee = valueWithoutComma;
            case "교육비" -> this.educationExpenses = valueWithoutComma;
            case "기부금" -> this.donation = valueWithoutComma;
            case "의료비" -> this.medicalExpenses = valueWithoutComma;
            case "퇴직연금" -> this.retirementPension = valueWithoutComma;
        }

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
