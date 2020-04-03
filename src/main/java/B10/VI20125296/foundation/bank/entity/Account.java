package B10.VI20125296.foundation.bank.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ACCOUNT")
@JsonIdentityInfo(property = "id", generator = ObjectIdGenerators.PropertyGenerator.class)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "ACCOUNT_NUMBER", unique = true)
    private Long accountNumber;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Column(name = "BALANCE")
    private Double balance;

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "CUSTOMER_ACCOUNT", joinColumns = @JoinColumn(name = "ACCOUNT_ID"), inverseJoinColumns = @JoinColumn(name = "CUSTOMER_ID"))
    private List<Customer> owners;

    public Account(Long accountNumber, AccountType accountType, Double balance){
        this.balance = balance;
        this.accountType = accountType;
        this.accountNumber = accountNumber;
    }
}
