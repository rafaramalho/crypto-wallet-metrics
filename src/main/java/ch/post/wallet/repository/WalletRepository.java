package ch.post.wallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ch.post.wallet.entity.Wallet;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    // Maybe one day custom queries can be added here
}
