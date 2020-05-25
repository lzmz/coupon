# Coupon 🎁
[![Build Status](https://travis-ci.org/lzmz/coupon.svg?branch=master)](https://travis-ci.org/lzmz/coupon)

This service is intended to handle gift coupons. Given a list of item identifiers and a maximum amount to spend, item prices will be recovered from MercadoLibre and a subset of those items will be returned. This set maximizes total spending but does not exceed the value of the coupon.

---

# How it works ? 💭
The given problem is a variant of the 0-1 Knapsack problem. From [Wikipedia](https://en.wikipedia.org/wiki/Knapsack_problem): Given a set of items, each with a weight and a value, determine the number of each item to include in a collection so that the total weight is less than or equal to a given limit and the total value is as large as possible.
This particular variant is known as [subset-sum problem](https://en.wikipedia.org/wiki/Subset_sum_problem) where each kind of item, the weight equals the value.
In our case, the weight / value of each item is the price, and, the bag capacity, the value of the coupon. Since these problems are considered NP-complete, no one has found a polynomial solution to solve them, but neither has it been demonstrated that such a solution does not exist. There is a [pseudo-polynomial time algorithm](https://en.wikipedia.org/wiki/Pseudo-polynomial_time) using [dynamic programming](https://en.wikipedia.org/wiki/Dynamic_programming) which was used to solve this particular case.

---

## Launch 🚀
> The application requires Redis installed to work. Install on [macOS](https://gist.github.com/tomysmile/1b8a321e7c58499ef9f9441b2faa0aa8) | [Linux](https://redis.io/topics/quickstart) | [Windows](https://redislabs.com/ebook/appendix-a/a-3-installing-on-windows/a-3-2-installing-redis-on-window/).

Generate the jar file:
```
$ mvn clean install
```
Run with one of these commands:
```
$ java -jar target/coupon-1.0.0-SNAPSHOT.jar
```
```
$ mvn spring-boot:run
```

---

## Tests 🔩
Run:
```
$ mvn test
```
JaCoCo code coverage report:
```
$ mvn jacoco:report
```
> You can take a look at target/site/jacoco/index.html page to see what the generated report looks like.

---

## Built with 🛠
* [IntelliJ IDEA](https://www.jetbrains.com/idea/) - IDE.
* [Spring Boot](https://spring.io/projects/spring-boot) - Backend framework.
* [OpenAPI 3.0 (Springdoc)](https://springdoc.org/) - API documentation.
* [Redis](https://redis.io/) - In-memory data structure store.
* [Maven](https://maven.apache.org/) - Dependency manager.

---

# API ☁
Once the server is running, you can access the [Swagger UI](http://localhost:8080/api/swagger-ui.html).

---

# Live demo 💻
The service is deployed in the PaaS provider [Pivotal Web Services (PWS)](https://run.pivotal.io/) and you can check the API documentation [here](https://apicoupon.cfapps.io/swagger-ui.html).

---

## Author 👦
> [GitHub](https://github.com/lzmz) &nbsp;&middot;&nbsp; [LinkedIn](https://www.linkedin.com/in/leonel-menendez/) &nbsp;&middot;&nbsp;