package com.artventuria.api.service.nfc.impl;

import com.artventuria.api.domain.postgresql.NFCTag;
import com.artventuria.api.dto.nfc.CreateNFCTagRequest;
import com.artventuria.api.dto.nfc.UpdateNFCTagRequest;
import com.artventuria.api.exception.ResourceNotFoundException;
import com.artventuria.api.repository.jpa.nfc.NFCTagRepository;
import com.artventuria.api.service.nfc.NFCTagService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NFCTagServiceImpl implements NFCTagService {
    
    private final NFCTagRepository nfcTagRepository;
    
    @Override
    @Transactional
    public NFCTag getTag(Integer id) {
        return nfcTagRepository.findById(id.longValue())
                .orElseThrow(() -> new ResourceNotFoundException("NFC tag not found"));
    }

    @Override
    @Transactional
    public NFCTag updateTag(Integer id, NFCTag updatedTag) {
        NFCTag tag = nfcTagRepository.findById(id.longValue())
                .orElseThrow(() -> new ResourceNotFoundException("NFC tag not found"));
        tag.setToken(updatedTag.getToken());
        tag.setActive(updatedTag.isActive());
        return nfcTagRepository.save(tag);
    }

    @Override
    @Transactional
    public void deleteTag(Integer id) {
        if (!nfcTagRepository.existsById(id.longValue())) {
            throw new ResourceNotFoundException("NFC tag not found");
        }
        nfcTagRepository.deleteById(id.longValue());
    }

    @Override
    @Transactional
    public NFCTag createNFCTag(CreateNFCTagRequest request) {
        NFCTag tag = new NFCTag();
        tag.setToken(request.getToken());
        tag.setActive(true);
        return nfcTagRepository.save(tag);
    }

    @Override
    @Transactional
    public NFCTag createTag(NFCTag tag) {
        return nfcTagRepository.save(tag);
    }

    @Override
    @Transactional(readOnly = true)
    public NFCTag getNFCTagByToken(String token) {
        return nfcTagRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("NFC tag not found"));
    }

    @Override
    @Transactional
    public NFCTag updateNFCTag(String token, UpdateNFCTagRequest request) {
        NFCTag tag = nfcTagRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("NFC tag not found"));
        tag.setActive(request.isActive());
        return nfcTagRepository.save(tag);
    }

    @Override
    @Transactional
    public void deleteNFCTag(String token) {
        NFCTag tag = nfcTagRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("NFC tag not found"));
        nfcTagRepository.delete(tag);
    }
}
