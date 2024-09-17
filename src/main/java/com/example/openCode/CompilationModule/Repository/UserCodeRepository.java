package com.example.openCode.CompilationModule.Repository;

import com.example.openCode.CompilationModule.Model.UserCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCodeRepository extends JpaRepository<UserCode, Long> {
}
