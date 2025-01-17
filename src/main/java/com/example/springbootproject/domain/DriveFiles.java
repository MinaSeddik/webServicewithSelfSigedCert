package com.example.springbootproject.domain;

import lombok.Data;
import lombok.ToString;

import java.io.File;
import java.io.Serializable;
import java.util.List;

@Data
@ToString
public class DriveFiles implements Serializable {
    private String kind;
    private String nextPageToken;
    private String incompleteSearch;
    private List<DriverFile> files;
}