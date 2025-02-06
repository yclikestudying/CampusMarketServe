package com.project.DTO;

import lombok.Data;

@Data
public class UploadFile {
    /**
     * 文本
     */
    private String text;

    /**
     * 多图片json
     */
    private String files;
}
