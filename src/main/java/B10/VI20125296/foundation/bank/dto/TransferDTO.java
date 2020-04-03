package B10.VI20125296.foundation.bank.dto;

import lombok.Data;

@Data
public class TransferDTO {
    private Long fromAccountId;
    private Long toAccountId;
    private Double amount;
}
