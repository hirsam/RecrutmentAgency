package com.example.recrutmentagencyapp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "vacancy")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Vacancy {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(cascade= CascadeType.ALL)
    public User user;

    @NotEmpty
    private String name;

    @NotEmpty
    private String desiredApplicantSkills;

    @NotEmpty
    private Integer upperWageLimit;

    @NotEmpty
    private Integer lowerWageLimit;

    @NotEmpty
    private Boolean socialPackage;
}
