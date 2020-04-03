package B10.VI20125296.foundation.bank.service;

import B10.VI20125296.foundation.bank.dto.TransferDTO;
import B10.VI20125296.foundation.bank.entity.Account;
import B10.VI20125296.foundation.bank.entity.Customer;
import B10.VI20125296.foundation.bank.repository.AccountRepository;
import B10.VI20125296.foundation.bank.repository.CustomerRepository;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class AccountService {

    private AccountRepository accountRepository;
    private CustomerRepository customerRepository;

    public AccountService(AccountRepository accountRepository, CustomerRepository customerRepository){
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public ResponseEntity<Account> addAccount(Long customerId, Account account) throws Exception {
        Optional<Customer> customerOptional = this.customerRepository.findById(customerId);
        Customer customer = customerOptional.orElseThrow(ChangeSetPersister.NotFoundException::new);

        account = this.accountRepository.save(account);
        if(account.getOwners() == null){
            account.setOwners(new ArrayList<>());
        }
        account.getOwners().add(customer);

        this.accountRepository.save(account);
        return ResponseEntity.ok(account);
    }

    @Transactional
    public ResponseEntity<Account> addOwner(Long accountId, Long customerId) throws Exception {
        Optional<Customer> customerOptional = this.customerRepository.findById(customerId);
        Customer customer = customerOptional.orElseThrow(ChangeSetPersister.NotFoundException::new);

        Optional<Account> accountOptional = this.accountRepository.findById(accountId);
        Account account = accountOptional.orElseThrow(ChangeSetPersister.NotFoundException::new);

        account.getOwners().add(customer);
        return ResponseEntity.ok(this.accountRepository.save(account));
    }

    @Transactional
    public ResponseEntity<Account> findById(Long id) throws Exception {
        Optional<Account> accountOptional = this.accountRepository.findById(id);
        Account account = accountOptional.orElseThrow(ChangeSetPersister.NotFoundException::new);
        account.getOwners();

        return ResponseEntity.ok(account);
    }

    @Transactional
    public ResponseEntity<Account> transferFunds(TransferDTO transferDTO) throws Exception {
        Assert.isTrue(transferDTO.getFromAccountId() != transferDTO.getToAccountId(),
                "From and to accounts must be different");

        Optional<Account> fromAccountOptional = this.accountRepository.findById(transferDTO.getFromAccountId());
        Account fromAccount = fromAccountOptional.orElseThrow(ChangeSetPersister.NotFoundException::new);

        Optional<Account> toAccountOptional = this.accountRepository.findById(transferDTO.getToAccountId());
        Account toAccount = toAccountOptional.orElseThrow(ChangeSetPersister.NotFoundException::new);

        Assert.isTrue(fromAccount.getBalance() > transferDTO.getAmount(),
                "From account does not have enought funds to transfer");

        fromAccount.setBalance(fromAccount.getBalance() - transferDTO.getAmount());
        toAccount.setBalance(toAccount.getBalance() + transferDTO.getAmount());

        this.accountRepository.save(fromAccount);
        this.accountRepository.save(toAccount);

        return ResponseEntity.ok(toAccount);
    }
}
