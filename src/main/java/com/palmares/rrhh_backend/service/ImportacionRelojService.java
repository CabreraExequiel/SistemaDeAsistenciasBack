//package com.palmares.rrhh_backend.service;
//
//import com.palmares.rrhh_backend.dto.ImportacionRelojResponse;
//import org.springframework.web.multipart.MultipartFile;
//
//public interface ImportacionRelojService {
//    ImportacionRelojResponse importar(MultipartFile file, String usuario);
//}

package com.palmares.rrhh_backend.service;

import com.palmares.rrhh_backend.dto.ImportacionRelojResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ImportacionRelojService {
    ImportacionRelojResponse importar(MultipartFile file, String usuario);
}