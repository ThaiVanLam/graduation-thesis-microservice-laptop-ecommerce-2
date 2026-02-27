package com.ecommerce.user_service.service;


import com.ecommerce.user_service.payload.AddressDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface AddressService {
    AddressDTO createAddress(@Valid AddressDTO addressDTO);

    List<AddressDTO> getAllAddresses();

    AddressDTO getAddressById(Long addressId);

    List<AddressDTO> getAddressesByLoggedInUser();

    AddressDTO updateAddress(Long addressId, @Valid AddressDTO addressDTO);

    AddressDTO deleteAddress(Long addressId);
}
