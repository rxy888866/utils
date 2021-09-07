package com.example.utils.util.token;

import org.springframework.beans.factory.annotation.Value;

import java.util.Date;
import java.util.Map;

public class TokenUtil {

    //用户名的key
    private static final String CLAIM_KEY_USERNAME="sub";
    //签名的时间
    private static final String CLAIM_KEY_CREATED="created";

    /**
     * @Value的值有两类：
     * ① ${ property : default_value }
     * ② #{ obj.property? :default_value }
     * 第一个注入的是外部配置文件对应的property，第二个则是SpEL表达式对应的内容。 那个
     * default_value，就是前面的值为空时的默认值。注意二者的不同，#{}里面那个obj代表对象。
     */
//JWT密钥
    @Value("${jwt.secret}")
    private  String secret;

    //JWT失效时间
    @Value("${jwt.expiration}")
    private Long expiration;


    //根据用户信息生成token

    /**
     * 根据用户信息生成Token
     * @param userDetails
     * @return
     */
    public String generateToken(UserDetails userDetails){
        //荷载
        Map<String,Object> claim=new HashMap<>();
        claim.put(CLAIM_KEY_USERNAME,userDetails.getUsername());
        claim.put(CLAIM_KEY_CREATED,new Date());
        return generateToken(claim);
    }

    /**
     * 根据负载生成JWT Token
     * @param claims
     * @return
     */
    private String generateToken(Map<String,Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(generateExpirationDate())//添加失效时间
                .signWith(SignatureAlgorithm.HS512,secret)//添加密钥以及加密方式
                .compact();
    }

    /**
     * 生成Token失效时间  当前时间+配置的失效时间
     * @return
     */
    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis()+expiration*1000);
    }


    // 根据token 生成用户
    /**
     * 根据Token生成用户名
     * @param token
     * @return
     */
    public String getUsernameFormToken(String token){
        String username;
        //根据Token去拿荷载
        try {
            Claims claim=getClaimFromToken(token);
            username=claim.getSubject();//获取用户名
        } catch (Exception e) {
            e.printStackTrace();
            username=null;
        }
        return username;
    }

    /**
     * 从Token中获取荷载
     * @param token
     * @return
     */
    private Claims getClaimFromToken(String token) {
        Claims claims=null;
        try {
            claims=Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return claims;
    }

    //判断token是否有效

    /**
     * 判断Token是否有效
     * Token是否过期
     * Token中的username和UserDetails中的username是否一致
     * @param token
     * @param userDetails
     * @return
     */
    public boolean TokenIsValid(String token,UserDetails userDetails){
        String username = getUsernameFormToken(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * 判断Token是否过期
     * @param token
     * @return
     */
    private boolean isTokenExpired(String token) {
        //获取Token的失效时间
        Date expireDate=getExpiredDateFromToken(token);
        //在当前时间之前，则失效
        return expireDate.before(new Date());
    }

    /**
     * 获取Token的失效时间
     * @param token
     * @return
     */
    private Date getExpiredDateFromToken(String token) {
        Claims claims = getClaimFromToken(token);
        return claims.getExpiration();
    }


    //判断token是否可以被刷新

    /**
     * 判断token是否可用被刷新
     * 如果已经过期了，则可用被刷新，未过期，则不可用被刷新
     * @param token
     * @return
     */
    public boolean canRefresh(String token){
        return !isTokenExpired(token);
    }

    //刷新token 获取新token
    /**
     * 刷新Token
     * @param token
     * @return
     */
    public String refreshToken(String token){
        Claims claims=getClaimFromToken(token);
        claims.put(CLAIM_KEY_CREATED,new Date());
        return generateToken(claims);
    }

}
