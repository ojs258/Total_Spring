package com.example.crawlingTest.crawling;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class NaverRecentCrawlingServiceImpl implements CrawlingService{

    RecentNewsFilter rnf = new RecentNewsFilter(); // 최신뉴스
    @Override
    public List<String> crawling() { // 가져온 검색 기록을 서로 비교하여 공통된 부분만 추려내는 부분
        String responseBody = get(makeApiUrl());
        try {
            return bodyParsing(responseBody);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    private String makeApiUrl(){ // 유저가 최신 선택한 카테고리 해당하는 뉴스를 가져오는 부분
        String keyword = null;
        try {
            keyword = URLEncoder.encode(rnf.getCategory(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("검색어 인코딩 실패",e);
        }
        
        String query = "?query=" + keyword; // 검색 키워드가 들어가는부분 최신 기사 서비스에서는 카테고리만.
        String display = "&display=10"; // 몇번째 검색 기록까지 보여줄지 정하는 부분
        String start = "&start=1"; // 정렬 후 몇 번째 부터 보여줄지 정하는 부분
        String sort = "&sort=sim"; //sim -> 검색어 정확도순서 정렬(내림차순), date -> 검색어 날자순 정렬(내림차순)
        String apiUrl = "https://openapi.naver.com/v1/search/news.json" + query + display + start + sort;

        return apiUrl;
    }
    private List<String> bodyParsing(String responseBody) throws ParseException { // Naver검색API를 어댑터 패턴으로 사용하기 위해 API가 리턴한 JSON을 파싱해서 List<Sting>에 담아주는 메소드
        List<String> bodyList= new ArrayList<>();
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse((responseBody));
        JSONArray jsonArray = (JSONArray) jsonObject.get("items");
        for (Object json : jsonArray) {
            bodyList.add(json.toString());
        }
        return bodyList;
    }

    private String get(String apiUrl) {

        HttpURLConnection con = connect(apiUrl);
        try {
            con.setRequestMethod("GET"); // API를 GET방식으로 호출하는것으로 등록
            con.setRequestProperty("X-Naver-Client-Id","q7T37qBLF0PdgAjo97uG"); // API 아이디
            con.setRequestProperty("X-Naver-Client-Secret","vUhKjp4hYs"); // API 시크릿 key

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                return readBody(con.getInputStream());
            } else { // 오류 발생
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }

    private HttpURLConnection connect(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection)url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }


    private static String readBody(InputStream body){
        InputStreamReader streamReader = new InputStreamReader(body);


        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();


            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }


            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는 데 실패했습니다.", e);
        }
    }


}
