package com.example.librarymanager.service.impl;

import com.example.librarymanager.repository.BookAuthorRepository;
import com.example.librarymanager.service.BookAuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookAuthorServiceImpl implements BookAuthorService {

    private BookAuthorRepository bookAuthorRepository;

}