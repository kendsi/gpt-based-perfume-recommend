package com.acscent.chatdemo2.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.acscent.chatdemo2.model.UserCode;

@Repository
public interface UserCodeRepository extends JpaRepository<UserCode, Long> {
    Optional<UserCode> findByCode(String code);
}
