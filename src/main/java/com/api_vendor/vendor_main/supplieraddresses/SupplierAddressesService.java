
package com.api_vendor.vendor_main.supplieraddresses;

import com.api_vendor.vendor_main.suppliers.Suppliers;
import com.api_vendor.vendor_main.suppliers.SuppliersRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class SupplierAddressesService {

    private final SupplierAddressesRepository addressesRepository;
    private final SuppliersRepository suppliersRepository;

    public SupplierAddressesService(
            SupplierAddressesRepository addressesRepository,
            SuppliersRepository suppliersRepository
    ) {
        this.addressesRepository = addressesRepository;
        this.suppliersRepository = suppliersRepository;
    }

    @Transactional(readOnly = true)
    public List<SupplierAddressesDTO> getAll(Integer supplierId) {

        List<SupplierAddresses> addresses;

        if (supplierId != null) {
            Suppliers supplier = findSupplier(supplierId);
            addresses = addressesRepository.findAllBySupplier(supplier);
        } else {
            addresses = addressesRepository.findAll();
        }

        return addresses.stream()
                .map(SupplierAddressesMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public SupplierAddressesDTO getById(Integer id) {

        SupplierAddresses address = findAddress(id);

        return SupplierAddressesMapper.toDTO(address);
    }

    @Transactional
    public SupplierAddressesDTO create(SupplierAddressesDTO dto) {

        validate(dto);

        Suppliers supplier = findSupplier(dto.supplierId());

        SupplierAddresses address =
                SupplierAddressesMapper.toEntity(dto, supplier);

        SupplierAddresses saved = addressesRepository.save(address);

        return SupplierAddressesMapper.toDTO(saved);
    }

    @Transactional
    public SupplierAddressesDTO update(
            Integer id,
            SupplierAddressesDTO dto
    ) {
        validate(dto);

        SupplierAddresses address = findAddress(id);
        Suppliers supplier = findSupplier(dto.supplierId());

        SupplierAddressesMapper.updateEntity(
                address,
                dto,
                supplier
        );

        SupplierAddresses saved = addressesRepository.save(address);

        return SupplierAddressesMapper.toDTO(saved);
    }

    @Transactional
    public void delete(Integer id) {

        SupplierAddresses address = findAddress(id);

        addressesRepository.delete(address);
    }

    private Suppliers findSupplier(Integer id) {
        return suppliersRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Supplier not found"
                ));
    }

    private SupplierAddresses findAddress(Integer id) {
        return addressesRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Supplier address not found"
                ));
    }

    private void validate(SupplierAddressesDTO dto) {

        if (dto == null ||
                dto.supplierId() == null ||
                dto.streetAddress() == null ||
                dto.streetAddress().isBlank() ||
                dto.city() == null ||
                dto.city().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Supplier ID, street address and city are required"
            );
        }
    }
}
