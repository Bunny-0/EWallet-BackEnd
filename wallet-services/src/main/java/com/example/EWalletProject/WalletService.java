package com.example.EWalletProject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.Table;

@Service
public class WalletService {

    @Autowired
    WalletRepository walletRepository;
    public void createWallet(String userName){
        Wallet wallet=Wallet.builder().userName(userName).amount(0).build();
        walletRepository.save(wallet);


    }
    public Wallet incrementWallet(String userName,int amount){
        Wallet oldWallet= walletRepository.findByUserName(userName);
        int newAmount=oldWallet.getAmount()+amount;
        int originalId=oldWallet.getId();

        Wallet updateWallet=Wallet.builder().userName(userName).id(originalId).amount(newAmount).build();
        walletRepository.save(updateWallet);
        //another way (Through query)
//        int success =walletRepository.updateWallet(userName,amount);
        return updateWallet;

    }
    public Wallet decrementWallet(String userName,int amount){
        Wallet oldWallet= walletRepository.findByUserName(userName);
        int newAmount=oldWallet.getAmount()-amount;
        int originalId=oldWallet.getId();

        Wallet updateWallet=Wallet.builder().userName(userName).id(originalId).amount(newAmount).build();
        walletRepository.save(updateWallet);
        return updateWallet;
    }
}
