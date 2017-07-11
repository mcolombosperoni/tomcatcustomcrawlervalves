Crawler Session Manager Valve
=========

[![Build Status](https://travis-ci.org/mcolombosperoni/tomcatcustomcrawlervalves.svg?branch=master)](https://travis-ci.org/mcolombosperoni/tomcatcustomcrawlervalves.svg?branch=master)


A core library born to extends standard CrawlerSessionManagerValve functionalities.

you can found the base code CrawlerSessionManagerValve used at this link [tomcat80](https://raw.githubusercontent.com/apache/tomcat80/TOMCAT_8_0_32/java/org/apache/catalina/valves/CrawlerSessionManagerValve.java)

Maven project based on tomcat 7.0.55 and java 1.7.


## Contribution

_Appreciate any contribution for this project, including suggestions, documentation improvements, reporting issues, forks and bugfixs,  etc._

Thanks.

## Documentation

CrawlerSessionManagerValve: abstract class customized starting from the original tomcat one. 
The modification involved is the retrieval of remoteAddress, that originally was the one returned by `request.getRemoteAddr` and in this customization is made by an abstract method `getRemoteAddress(request)`.

StandardCrawlerSessionManagerImpl: first implementation of the abstract CrawlerSessionManagerValve that works as the original one retrieving the remote address from request as described above.

HeaderCrawlerSessionManagerValveImpl: implementation that retrieve the address from the header of the request. The standard header used is the `x-forwarded-for` but is settable by property `ipRequestHeaderKey`.

CookieCrawlerSessionManagerValveImpl: implementation that retrieve the address from the cookies of the request. The standard header used is the `enduserip` but is settable by property `ipRequestCookieKey`.

MultiPathHeaderCrawlerSessionManager: example and more specific header implementation that uses also the path country-language of a site in order to have an address key specific to avoid conflict sessions issues.
 
### Example

add into tomcat server.xml inside tomcat conf folder
```xml

 <Valve className="com.mcs.tomcat.impl.MultiPathHeaderCrawlerSessionManager"
        crawlerUserAgents="(?i).*(GoldenFeeds|facebookexternalhit|Googlebot|SpringBot|GomezAgent|Daum|pinterest\.com|Twitterbot|bing\.com|baiduspider|yahoo\.com|sogou\.com|applebot|yandex|googleweblight|Google\sWeb\sPreview|BingPreview|semrush\.com|ahrefs\.com|opensiteexplorer\.org|webmeup\-crawler.com|istellabot/t\.1|webmeup\-crawler\.com|Catchpoint|2locosbot|DejaClick|Linguee|sogou\.com|naver\.me|heritrix|Google\sPage\sSpeed\sInsights|Facebot|YisouSpider|AdsBot\-Google|ltx71\.com|moatbot|UptimeRobot|Scrapy).*"
        ipRequestHeaderKey="True-Client-IP"
        sessionInactiveInterval="60" 
 />

```

