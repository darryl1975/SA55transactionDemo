package sg.nus.iss.demoTransaction.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import sg.nus.iss.demoTransaction.dto.TransferResult;
import sg.nus.iss.demoTransaction.exception.AccountNotExistException;
import sg.nus.iss.demoTransaction.exception.CheckBalanceException;
import sg.nus.iss.demoTransaction.exception.OverDraftException;
import sg.nus.iss.demoTransaction.model.TransferRequest;
import sg.nus.iss.demoTransaction.service.AccountService;

@RestController
@RequestMapping("/v1/transaction")
@Api(tags = {"Transaction Controller"}, description = "Provide APIs for transaction related operation")
public class TransactionController {
    private static final Logger log = LoggerFactory.getLogger(TransactionController.class);
	
	@Autowired
	private AccountService accountService;

	@PostMapping(consumes = { "application/json" })
	@ApiOperation(value = "API to create transaction", response = TransferResult.class, produces = "application/json")
	public ResponseEntity transferMoney(@RequestBody @Valid TransferRequest request) throws Exception {

		try {
			accountService.transferBalances(request);
			
			TransferResult result = new TransferResult();
			result.setAccountFromId(request.getAccountFromId());
			result.setBalanceAfterTransfer(accountService.checkBalance(request.getAccountFromId()));
			
			return new ResponseEntity<>(result, HttpStatus.ACCEPTED);
		} catch (AccountNotExistException | OverDraftException e) {
			log.error("Fail to transfer balances, please check with system administrator.");
			throw e;
		} catch (CheckBalanceException cbEx) {
			log.error("Fail to check balances after transfer, please check with system administrator.");
			throw cbEx;
		}
	}
}
