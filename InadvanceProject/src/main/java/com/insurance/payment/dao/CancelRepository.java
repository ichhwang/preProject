package com.insurance.payment.dao;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.insurance.payment.entity.Cancel;

@Repository
public interface CancelRepository extends JpaRepository<Cancel, String> {

}
