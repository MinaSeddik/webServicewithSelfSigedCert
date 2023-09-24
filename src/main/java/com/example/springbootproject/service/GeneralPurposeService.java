package com.example.springbootproject.service;

import com.example.springbootproject.repository.impl.StoredProcedureRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GeneralPurposeService {

    @Autowired
    private StoredProcedureRepository storedProcedureRepository;

    public void callStoredProc() {

        storedProcedureRepository.callStoredProcedure();
    }
}
