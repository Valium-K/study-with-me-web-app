package dev.valium.studywithmewebapp.service;

import dev.valium.studywithmewebapp.domain.Account;
import dev.valium.studywithmewebapp.domain.Account2Tag;
import dev.valium.studywithmewebapp.domain.Tag;
import dev.valium.studywithmewebapp.repository.Account2TagRepository;
import dev.valium.studywithmewebapp.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class Account2TagService {

    private final Account2TagRepository account2TagRepository;

    public void addTag(Account account, Tag tag) {
        Optional<Account2Tag> foundAccount = account2TagRepository.findTag(account.getId());

        foundAccount.ifPresent(at -> {
            at.setAccount(account);
            at.setTag(tag);

            account2TagRepository.save(at);
        });


    }
}
