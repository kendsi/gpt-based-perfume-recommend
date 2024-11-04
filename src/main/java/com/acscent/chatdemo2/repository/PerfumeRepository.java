package com.acscent.chatdemo2.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acscent.chatdemo2.model.Perfume;

public interface PerfumeRepository extends JpaRepository<Perfume, Long> {
}
