package com.bit.boardappbackend.repository;

import com.bit.boardappbackend.entity.BoardFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardFileRepository extends JpaRepository<BoardFile, Long> {
}
