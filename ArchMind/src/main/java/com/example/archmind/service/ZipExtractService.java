package com.example.archmind.service;

import java.nio.file.Path;

public interface ZipExtractService {
    Path extract(Path ZipFile,Path targetDirectory);
}
