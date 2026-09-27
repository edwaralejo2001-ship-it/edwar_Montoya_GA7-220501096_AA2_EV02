package com.ewuarsoft.service;

import com.ewuarsoft.model.Cliente;
import com.ewuarsoft.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    public List<Cliente> buscar(String query) {
        if (query == null || query.trim().isEmpty()) {
            return clienteRepository.findAll();
        }
        return clienteRepository.buscarPorNombreODocumento(query.trim());
    }

    public boolean existeDocumento(String documento) {
        return clienteRepository.existsByDocumento(documento);
    }

    @Transactional
    public Cliente guardar(Cliente cliente) {
        if (cliente.getId() == null && clienteRepository.existsByDocumento(cliente.getDocumento())) {
            throw new IllegalArgumentException("Ya existe un cliente con el documento " + cliente.getDocumento());
        }
        return clienteRepository.save(cliente);
    }

    @Transactional
    public void eliminar(Long id) {
        clienteRepository.deleteById(id);
    }

    public long contarClientes() {
        return clienteRepository.count();
    }
}
