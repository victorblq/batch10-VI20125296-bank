package B10.VI20125296.foundation.bank.service;

import B10.VI20125296.foundation.bank.dto.TransferDTO;
import B10.VI20125296.foundation.bank.entity.Account;
import B10.VI20125296.foundation.bank.entity.AccountType;
import B10.VI20125296.foundation.bank.entity.Customer;
import B10.VI20125296.foundation.bank.repository.AccountRepository;
import B10.VI20125296.foundation.bank.repository.CustomerRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Optional;

@RunWith(SpringRunner.class)
public class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private AccountRepository accountRepository;

    @Test
    public void testAddAccountPass() throws Exception {
        Customer customer = new Customer(1L);
        Account account =  new Account(1000L, AccountType.CURRENT, 1000D);

        Mockito.when(this.customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        Mockito.when(this.accountRepository.save(account)).thenReturn(account);

        ResponseEntity<Account> response = this.accountService.addAccount(customer.getId(), account);
        Assert.assertNotNull(response);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test(expected = ChangeSetPersister.NotFoundException.class)
    public void testAddAccountFailOnCustomerNotFound() throws Exception{
        Customer customer = new Customer(1L);
        Account account =  new Account(1000L, AccountType.CURRENT, 1000D);

        Mockito.when(this.customerRepository.findById(customer.getId())).thenReturn(Optional.empty());
        Mockito.when(this.accountRepository.save(account)).thenReturn(account);

        this.accountService.addAccount(customer.getId(), account);
    }

    @Test
    public void testFindByIdPass() throws Exception {
        Account account =  new Account(1000L, AccountType.CURRENT, 1000D);
        account.setId(1L);

        Mockito.when(this.accountRepository.findById(1L)).thenReturn(Optional.of(account));

        ResponseEntity<Account> response = this.accountService.findById(1L);
        Assert.assertNotNull(response);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test(expected = ChangeSetPersister.NotFoundException.class)
    public void testFindByIdFail() throws Exception {
        Account account =  new Account(1000L, AccountType.CURRENT, 1000D);
        account.setId(1L);

        Mockito.when(this.accountRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Account> response = this.accountService.findById(1L);
    }

    @Test
    public void testAddOwnerPass() throws Exception {
        Customer customer1 = new Customer(1L);
        Customer customer2 = new Customer(2L);
        Account account =  new Account(1000L, AccountType.CURRENT, 1000D);
        account.setId(1L);
        account.setOwners(new ArrayList<>());
        account.getOwners().add(customer1);

        Account accountWithNewOwner =  new Account(1000L, AccountType.CURRENT, 1000D);
        accountWithNewOwner.setId(1L);
        accountWithNewOwner.setOwners(new ArrayList<>());
        accountWithNewOwner.getOwners().add(customer1);
        accountWithNewOwner.getOwners().add(customer2);

        Mockito.when(this.accountRepository.save(account)).thenReturn(accountWithNewOwner);
        Mockito.when(this.accountRepository.findById(account.getId())).thenReturn(Optional.of(account));
        Mockito.when(this.customerRepository.findById(customer2.getId())).thenReturn(Optional.of(customer2));

        ResponseEntity<Account> response = this.accountService.addOwner(account.getId(), customer2.getId());
        Assert.assertNotNull(response);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(2, response.getBody().getOwners().size());
    }

    @Test(expected = ChangeSetPersister.NotFoundException.class)
    public void testAddOwnerFailCustomerNotFound() throws Exception {
        Customer customer1 = new Customer(1L);
        Customer customer2 = new Customer(2L);
        Account account =  new Account(1000L, AccountType.CURRENT, 1000D);
        account.setId(1L);
        account.setOwners(new ArrayList<>());
        account.getOwners().add(customer1);

        Mockito.when(this.accountRepository.findById(account.getId())).thenReturn(Optional.of(account));
        Mockito.when(this.customerRepository.findById(customer2.getId())).thenReturn(Optional.empty());

        this.accountService.addOwner(account.getId(), customer2.getId());
    }

    @Test
    public void testTransferFundsPass() throws Exception{
        Account fromAccount = new Account(1000L, AccountType.CURRENT, 100D);
        fromAccount.setId(1L);
        Account toAccount = new Account(1001L, AccountType.CURRENT, 100D);
        toAccount.setId(2L);

        TransferDTO transferDTO = new TransferDTO();
        transferDTO.setFromAccountId(fromAccount.getId());
        transferDTO.setToAccountId(toAccount.getId());
        transferDTO.setAmount(10D);

        Mockito.when(this.accountRepository.findById(transferDTO.getFromAccountId())).thenReturn(Optional.of(fromAccount));
        Mockito.when(this.accountRepository.findById(transferDTO.getToAccountId())).thenReturn(Optional.of(toAccount));
        Mockito.when(this.accountRepository.save(fromAccount)).thenReturn(fromAccount);
        Mockito.when(this.accountRepository.save(toAccount)).thenReturn(toAccount);

        ResponseEntity<Account> responseEntity = this.accountService.transferFunds(transferDTO);
        Assert.assertNotNull(responseEntity);
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assert.assertEquals(new Double(90), fromAccount.getBalance());
        Assert.assertEquals(new Double(110), toAccount.getBalance());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTransferFundsFailFromAccountDoesNotHaveFunds() throws Exception{
        Account fromAccount = new Account(1000L, AccountType.CURRENT, 0D);
        fromAccount.setId(1L);
        Account toAccount = new Account(1001L, AccountType.CURRENT, 100D);
        toAccount.setId(2L);

        TransferDTO transferDTO = new TransferDTO();
        transferDTO.setFromAccountId(fromAccount.getId());
        transferDTO.setToAccountId(toAccount.getId());
        transferDTO.setAmount(10D);

        Mockito.when(this.accountRepository.findById(transferDTO.getFromAccountId())).thenReturn(Optional.of(fromAccount));
        Mockito.when(this.accountRepository.findById(transferDTO.getToAccountId())).thenReturn(Optional.of(toAccount));
        Mockito.when(this.accountRepository.save(fromAccount)).thenReturn(fromAccount);
        Mockito.when(this.accountRepository.save(toAccount)).thenReturn(toAccount);

        this.accountService.transferFunds(transferDTO);
    }

    @Test(expected = ChangeSetPersister.NotFoundException.class)
    public void testTransferFundsFailAccountNotFound() throws Exception{
        Account fromAccount = new Account(1000L, AccountType.CURRENT, 0D);
        fromAccount.setId(1L);
        Account toAccount = new Account(1001L, AccountType.CURRENT, 100D);
        toAccount.setId(2L);

        TransferDTO transferDTO = new TransferDTO();
        transferDTO.setFromAccountId(fromAccount.getId());
        transferDTO.setToAccountId(toAccount.getId());
        transferDTO.setAmount(10D);

        Mockito.when(this.accountRepository.findById(transferDTO.getFromAccountId())).thenReturn(Optional.empty());
        Mockito.when(this.accountRepository.findById(transferDTO.getToAccountId())).thenReturn(Optional.of(toAccount));
        Mockito.when(this.accountRepository.save(fromAccount)).thenReturn(fromAccount);
        Mockito.when(this.accountRepository.save(toAccount)).thenReturn(toAccount);

        this.accountService.transferFunds(transferDTO);
    }
}
