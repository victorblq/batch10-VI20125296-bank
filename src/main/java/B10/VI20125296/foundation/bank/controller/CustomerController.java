package B10.VI20125296.foundation.bank.controller;

import B10.VI20125296.foundation.bank.entity.Account;
import B10.VI20125296.foundation.bank.entity.Customer;
import B10.VI20125296.foundation.bank.repository.CustomerRepository;
import B10.VI20125296.foundation.bank.service.AccountService;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    private AccountService accountService;
    private CustomerRepository customerRepository;

    public CustomerController(AccountService accountService, CustomerRepository customerRepository){
        this.accountService = accountService;
        this.customerRepository = customerRepository;
    }

    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers(){
        return ResponseEntity.ok().body(this.customerRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable("id") Long id) throws Exception{
        return ResponseEntity.ok().body(this.customerRepository.findById(id).orElseThrow(ChangeSetPersister.NotFoundException::new));
    }

    @Transactional
    @PostMapping("/{id}/accounts")
    public ResponseEntity<Account> addAccountForCustomer(@PathVariable("id") Long id, @RequestBody Account account) throws Exception{
        return this.accountService.addAccount(id, account);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<Customer> addCustomer(@RequestBody Customer customer){
        return ResponseEntity.ok(this.customerRepository.save(customer));
    }

    @PutMapping
    @Transactional
    public ResponseEntity<Customer> updateCustomer(@RequestBody Customer customer){
        return ResponseEntity.ok(this.customerRepository.save(customer));
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteCustomer(@PathVariable("id") Long id){
        this.customerRepository.deleteById(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
