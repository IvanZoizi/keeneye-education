package keenay.education.service.impl;

import keenay.education.dto.users.SellerBodyDTO;
import keenay.education.dto.users.SellerDTO;
import keenay.education.entity.Sellers;
import keenay.education.exception.errors.UserIsNotFoundException;
import keenay.education.mapper.seller.SellerMapper;
import keenay.education.repository.jpa.SellersRepository;
import keenay.education.repository.tarantool.SellersCacheRepository;
import keenay.education.security.CustomUserDetail;
import keenay.education.service.SellerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SellerServiceImpl implements SellerService {

    private final SellersCacheRepository sellersCacheRepository;
    private final SellersRepository sellersRepository;
    private final SellerMapper sellerMapper;

    @Override
    @Cacheable(value = "sellers", key = "#userDetail.sellerId")
    public SellerDTO getSeller(CustomUserDetail userDetail) {
        return sellerMapper.getSellerDTO(
                sellersRepository.findById(userDetail.getSellerId())
                        .orElseThrow(() -> new UserIsNotFoundException("User is not found"))
        );
    }

    @Override
    @CachePut(value = "sellers", key = "#userDetail.sellerId")
    public SellerDTO updateSeller(CustomUserDetail userDetail, SellerBodyDTO sellerBodyDTO) {
        System.out.println(sellerBodyDTO);
        List<Sellers> sellers = sellersRepository.update(
                userDetail.getSellerId(),
                sellerBodyDTO.getName(),
                sellerBodyDTO.getSurname(),
                sellerBodyDTO.getAddress(),
                sellerBodyDTO.getDescription(),
                sellerBodyDTO.getInn()
        );
        if (sellers.isEmpty()) {
            throw new UserIsNotFoundException("User is not found");
        }
        return sellerMapper.getSellerDTO(sellers.get(0));
    }
}
