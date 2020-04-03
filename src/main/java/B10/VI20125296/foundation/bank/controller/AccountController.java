package B10.VI20125296.foundation.bank.controller;

import B10.VI20125296.foundation.bank.dto.TransferDTO;
import B10.VI20125296.foundation.bank.entity.Account;
import B10.VI20125296.foundation.bank.repository.AccountRepository;
import B10.VI20125296.foundation.bank.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;

@RestController
@RequestMapping("/account")
public class AccountController {

    private AccountService accountService;
    private AccountRepository accountRepository;

    public AccountController(AccountService accountService, AccountRepository accountRepository){
        this.accountService = accountService;
        this.accountRepository = accountRepository;
    }

    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts(){
        return ResponseEntity.ok(this.accountRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable("id") Long id) throws Exception {
        return this.accountService.findById(id);
    }

    @PutMapping
    @Transactional
    public ResponseEntity<Account> updateAccount(@RequestBody Account account){
        return ResponseEntity.ok(this.accountRepository.save(account));
    }

    @Transactional
    @PutMapping("/add-owner/{accountId}/{customerId}")
    public ResponseEntity<Account> updateAccount(@PathVariable("accountId") Long accountId,
                                                 @PathVariable("customerId") Long customerId) throws Exception{
        return this.accountService.addOwner(accountId, customerId);
    }

    @Transactional
    @PutMapping("/transfer-funds")
    public ResponseEntity<Account> transferFunds(@RequestBody TransferDTO transferDTO) throws Exception{
        return this.accountService.transferFunds(transferDTO);
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteAccount(@PathVariable("id") Long id){
        this.accountRepository.deleteById(id);
        return ResponseEntity.ok().body(HttpStatus.OK);
    }
}
