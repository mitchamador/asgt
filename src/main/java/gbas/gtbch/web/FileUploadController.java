package gbas.gtbch.web;

import gbas.gtbch.model.FileUploadResponse;
import gbas.gtbch.util.Syncronizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static gbas.gtbch.util.Syncronizer.SYNCRONIZER_UPLOAD_PARAM;

@Controller
public class FileUploadController {

    @Autowired
    private Syncronizer syncronizer;

    // Handling file upload request
    @RequestMapping(value = "api/upload", method = RequestMethod.POST)
    public ResponseEntity<?> fileUpload(@RequestParam("file") MultipartFile file, @RequestParam("type") String type)
            throws IOException {

        FileUploadResponse response = new FileUploadResponse();

        response.setFileName(file.getOriginalFilename());
        response.setFileLength(file.getBytes().length);
        response.setMessage("Файл успешно загружен");

        if (SYNCRONIZER_UPLOAD_PARAM.equals(type)) {
            syncronizer.setBytes(file.getBytes());
        }

        return ResponseEntity.ok(response);
    }


}