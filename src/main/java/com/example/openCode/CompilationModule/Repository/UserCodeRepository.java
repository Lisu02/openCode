package com.example.openCode.CompilationModule.Repository;

import com.example.openCode.CompilationModule.Model.PlaygroundCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCodeRepository extends JpaRepository<PlaygroundCode, Long> {
}
