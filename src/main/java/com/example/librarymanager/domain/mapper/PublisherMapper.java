package com.example.librarymanager.domain.mapper;

import com.example.librarymanager.domain.dto.request.PublisherRequestDto;
import com.example.librarymanager.domain.entity.Publisher;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PublisherMapper {
    Publisher toPublisher(PublisherRequestDto requestDto);
}
