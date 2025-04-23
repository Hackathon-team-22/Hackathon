// src/test/java/com/example/finmonitor/api/TestReferenceDataHelper.java

package com.example.finmonitor.api;

import com.example.finmonitor.domain.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TestReferenceDataHelper {

    @Autowired
    private PartyTypeRepository partyTypeRepository;
    @Autowired
    private TransactionTypeRepository transactionTypeRepository;
    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private BankRepository bankRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String ensurePartyType(String baseName) {
        return partyTypeRepository.getOrCreateByName(baseName).toString();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String ensureTransactionType(String baseName) {
        return transactionTypeRepository.getOrCreateByName(baseName).toString();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String ensureStatus(String baseName) {
        return statusRepository.getOrCreateByName(baseName).toString();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String ensureBank(String baseName) {
        return bankRepository.getOrCreateByName(baseName).toString();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String ensureCategory(String baseName) {
        return categoryRepository.getOrCreateByName(baseName).toString();
    }
}
