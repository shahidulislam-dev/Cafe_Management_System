package com.cafeManagement.serviceImpl;

import com.cafeManagement.POJO.Category;
import com.cafeManagement.POJO.Products;
import com.cafeManagement.constant.CafeConstants;
import com.cafeManagement.dao.ProductDao;
import com.cafeManagement.jwt.JwtFilter;
import com.cafeManagement.service.ProductService;
import com.cafeManagement.utils.CafeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    JwtFilter jwtFilter;
    @Autowired
    ProductDao productDao;
    @Override
    public ResponseEntity<String> addNewProduct(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()){
                if(validateProductMap(requestMap, false)){
                    productDao.save(getProductFromMap(requestMap, false));
                    return CafeUtils.getResponseEntity("Product saved successfully",HttpStatus.OK);
                }
                return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA,HttpStatus.BAD_REQUEST);
            }else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS,HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private boolean validateProductMap(Map<String, String> requestMap, boolean validateId) {
        if(requestMap.containsKey("name")){
            if(requestMap.containsKey("id") && validateId){
                return true;
            }else if (!validateId) {
                return true;
            }
        }
        return false;
    }

    private Products getProductFromMap(Map<String, String> requestMap, boolean isAdd){
        Category category = new Category();
        category.setId(Integer.parseInt(requestMap.get("categoryId")));
        Products products = new Products();
        if(isAdd){
            products.setId(Integer.parseInt(requestMap.get("id")));
        }else {
            products.setStatus("true");
        }
        products.setCategory(category);
        products.setName(requestMap.get("name"));
        products.setDescription(requestMap.get("description"));
        products.setPrice(Integer.parseInt(requestMap.get("price")));
        return products;
    }
}
