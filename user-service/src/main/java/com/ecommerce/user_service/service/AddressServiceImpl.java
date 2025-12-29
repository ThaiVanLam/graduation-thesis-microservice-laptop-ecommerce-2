package com.ecommerce.user_service.service;


import com.ecommerce.user_service.exceptions.APIException;
import com.ecommerce.user_service.exceptions.ResourceNotFoundException;
import com.ecommerce.user_service.model.Address;
import com.ecommerce.user_service.model.User;
import com.ecommerce.user_service.payload.AddressDTO;
import com.ecommerce.user_service.repositories.AddressRepository;
import com.ecommerce.user_service.repositories.UserRepository;
import com.ecommerce.user_service.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressServiceImpl implements AddressService {
    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AuthUtil authUtil;

    @Override
    public AddressDTO createAddress(AddressDTO addressDTO) {
        // Get current logged-in user
        User user = authUtil.loggedInUser();

        // Map DTO to Entity
        Address address = modelMapper.map(addressDTO, Address.class);

        // Set user to address
        address.setUser(user);

        // Check if address already exists for this user
        List<Address> userAddresses = user.getAddresses();
        boolean addressExists = userAddresses.stream()
                .anyMatch(a ->
                        a.getStreet().equalsIgnoreCase(address.getStreet()) &&
                                a.getBuildingName().equalsIgnoreCase(address.getBuildingName()) &&
                                a.getCity().equalsIgnoreCase(address.getCity()) &&
                                a.getState().equalsIgnoreCase(address.getState()) &&
                                a.getPincode().equalsIgnoreCase(address.getPincode())
                );

        if (addressExists) {
            throw new APIException("Address already exists for this user");
        }

        // Save address (cascade will automatically add to user's address list)
        Address savedAddress = addressRepository.save(address);

        // Map entity back to DTO and return
        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAllAddresses() {
        // Get all addresses from database
        List<Address> addresses = addressRepository.findAll();

        if (addresses.isEmpty()) {
            throw new APIException("No addresses found");
        }

        // Map list of entities to list of DTOs
        return addresses.stream()
                .map(address -> modelMapper.map(address, AddressDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public AddressDTO getAddressById(Long addressId) {
        // Find address by ID
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));

        // Map entity to DTO and return
        return modelMapper.map(address, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAddressesByLoggedInUser() {
        // Get current logged-in user
        User user = authUtil.loggedInUser();

        // Get all addresses for this user
        List<Address> addresses = user.getAddresses();

        if (addresses.isEmpty()) {
            throw new APIException("No addresses found for this user");
        }

        // Map list of entities to list of DTOs
        return addresses.stream()
                .map(address -> modelMapper.map(address, AddressDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public AddressDTO updateAddress(Long addressId, AddressDTO addressDTO) {
        // Find existing address by ID
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));

        // Update address fields
        address.setStreet(addressDTO.getStreet());
        address.setBuildingName(addressDTO.getBuildingName());
        address.setCity(addressDTO.getCity());
        address.setState(addressDTO.getState());
        address.setCountry(addressDTO.getCountry());
        address.setPincode(addressDTO.getPincode());

        // Save updated address
        Address updatedAddress = addressRepository.save(address);

        // Map entity back to DTO and return
        return modelMapper.map(updatedAddress, AddressDTO.class);
    }

    @Override
    public AddressDTO deleteAddress(Long addressId) {
        // Find address by ID
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));

        // Map to DTO before deleting
        AddressDTO addressDTO = modelMapper.map(address, AddressDTO.class);

        // Delete address
        addressRepository.delete(address);

        // Return deleted address DTO
        return addressDTO;
    }
}
