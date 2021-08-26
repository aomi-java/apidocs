package tech.aomi.apidocs.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sean createAt 2021/8/24
 */
@Getter
@Setter
@ToString
public class Api {

    private String title;

    private String describe;

    private List<String> methods;

    private List<String> urls;

    private List<ApiField> headers = new ArrayList<>();

    private List<ApiField> path = new ArrayList<>();

    private List<ApiField> queryString = new ArrayList<>();

    private List<ApiField> formData = new ArrayList<>();

    private List<ApiField> body = new ArrayList<>();

    private String responseDescribe;
    private List<ApiField> responseHeaders = new ArrayList<>();

    private List<ApiField> responseBody = new ArrayList<>();
}
