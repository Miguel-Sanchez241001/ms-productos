package com.examen.productos.service;

import com.examen.productos.dto.ProductoRequestDTO;
import com.examen.productos.dto.ProductoResponseDTO;
import com.examen.productos.entity.Producto;
import com.examen.productos.exception.ProductoNotFoundException;
import com.examen.productos.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductoService {

    private final ProductoRepository repository;

    public ProductoService(ProductoRepository repository) {
        this.repository = repository;
    }

    public ProductoResponseDTO crear(ProductoRequestDTO dto) {
        Producto producto = new Producto();
        mapearDesdeDTO(dto, producto);
        return toResponse(repository.save(producto));
    }

    public List<ProductoResponseDTO> listar() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ProductoResponseDTO buscarPorId(Long id) {
        Producto producto = repository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException(id));
        return toResponse(producto);
    }

    public ProductoResponseDTO actualizar(Long id, ProductoRequestDTO dto) {
        Producto producto = repository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException(id));
        mapearDesdeDTO(dto, producto);
        return toResponse(repository.save(producto));
    }

    public void eliminar(Long id) {
        Producto producto = repository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException(id));
        producto.setEstado(false);
        repository.save(producto);
    }

    private void mapearDesdeDTO(ProductoRequestDTO dto, Producto producto) {
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock());
        if (dto.getEstado() != null) {
            producto.setEstado(dto.getEstado());
        }
    }

    private ProductoResponseDTO toResponse(Producto producto) {
        ProductoResponseDTO response = new ProductoResponseDTO();
        response.setId(producto.getId());
        response.setNombre(producto.getNombre());
        response.setDescripcion(producto.getDescripcion());
        response.setPrecio(producto.getPrecio());
        response.setStock(producto.getStock());
        response.setEstado(producto.getEstado());
        response.setFechaCreacion(producto.getFechaCreacion());
        return response;
    }
}
