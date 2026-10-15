package com.api_vendor.vendor_main.suppliers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SuppliersService {

    private final SuppliersRepository suppliersRepository;

    public SuppliersService(
            SuppliersRepository suppliersRepository
    ) {
        this.suppliersRepository = suppliersRepository;
    }

    @Transactional(readOnly = true)
    public Page<SuppliersDTO> getAll(
            String name,
            int page,
            int size
    ) {
        validatePagination(page, size);

        PageRequest pageable = PageRequest.of(
                page,
                size,
                Sort.by("suppliersID").ascending()
        );

        Page<Suppliers> suppliers;

        if (name == null || name.isBlank()) {
            suppliers = suppliersRepository.findAll(pageable);
        } else {
            suppliers = suppliersRepository
                    .findByNameContainingIgnoreCase(
                            name.trim(),
                            pageable
                    );
        }

        return suppliers.map(SuppliersMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public SuppliersDTO getById(Integer id) {
        return SuppliersMapper.toDTO(findSupplier(id));
    }

    @Transactional
    public SuppliersDTO create(SuppliersDTO dto) {
        validate(dto);

        Suppliers supplier = SuppliersMapper.toEntity(dto);

        Suppliers saved = suppliersRepository.save(supplier);

        return SuppliersMapper.toDTO(saved);
    }

    @Transactional
    public SuppliersDTO update(
            Integer id,
            SuppliersDTO dto
    ) {
        validate(dto);

        Suppliers supplier = findSupplier(id);

        SuppliersMapper.updateEntity(supplier, dto);

        Suppliers saved = suppliersRepository.save(supplier);

        return SuppliersMapper.toDTO(saved);
    }

    @Transactional
    public void delete(Integer id) {

        Suppliers supplier = findSupplier(id);

        if (suppliersRepository.countProductsBySupplierId(id) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Supplier has products and cannot be deleted"
            );
        }

        if (suppliersRepository.countAddressesBySupplierId(id) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Supplier has addresses and cannot be deleted"
            );
        }

        suppliersRepository.delete(supplier);
        suppliersRepository.flush();
    }

    private Suppliers findSupplier(Integer id) {

        if (id == null || id <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid supplier ID"
            );
        }

        return suppliersRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Supplier not found"
                ));
    }

    private void validate(SuppliersDTO dto) {

        if (dto == null ||
                dto.name() == null ||
                dto.name().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Supplier name is required"
            );
        }
    }

    private void validatePagination(int page, int size) {

        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid pagination parameters"
            );
        }
    }
}
