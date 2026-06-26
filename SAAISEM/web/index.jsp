<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    HttpSession sesion = request.getSession();
    String info = null;

%>
<!DOCTYPE html>
<html lang="EN">
    <head>
        <!-- Basic -->
        <meta charset="utf-8" />
        <meta http-equiv="X-UA-Compatible" content="IE=edge" />
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
        <meta name="keywords" content="" />
        <meta name="description" content="" />
        <meta name="author" content="" />

        <title>SIALSS CEDIS</title>

        <!-- slider stylesheet -->
        <link rel="stylesheet" type="text/css"  href="https://cdnjs.cloudflare.com/ajax/libs/OwlCarousel2/2.1.3/assets/owl.carousel.min.css" />
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css" />
        <link rel="stylesheet" type="text/css" href="cssindex/bootstrap.css" />
        <link href="https://fonts.googleapis.com/css?family=Poppins:400,600,700|Roboto:400,700&display=swap" rel="stylesheet" />

        <!-- Custom styles for this template -->
 
        <link href="cssindex/responsive.css" rel="stylesheet" />
        <link href="cssindex/bootstrap.css" rel="stylesheet" type="text/css"/>
        <link href="cssindex/style.css" rel="stylesheet" type="text/css"/>
    </head>

    <body>

        <div class="hero_area">
            <!-- header section strats -->
            <header class="header_section">

            </header>
            <!-- end header section -->
            <!-- slider section -->
            <section class=" slider_section position-relative">
                <div id="carouselExampleIndicators" class="carousel slide" data-ride="carousel">
                    <ol class="carousel-indicators">
                        <li data-target="#carouselExampleIndicators" data-slide-to="0" class="active"></li>
                        <li data-target="#carouselExampleIndicators" data-slide-to="1"></li>
                    </ol>
                    <div class="carousel-inner">
                        <div class="carousel-item active">
                            <div class="container">
                                <div class="row align-items-center">
                                    <div class="col-md-4">
                                        <div class="img-box">
                                            <img src="imagenes/alm6.jpg" alt="">
                                        </div>
                                    </div>
                                    <div class="col-md-8">
                                        <div class="detail-box">`
                                            <h1> <span>SIALSS CEDIS</span> </h1>

                                            <a href="/SIALSS_MDF/indexalmacen.jsp">SIALSS ALMACÉN</a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="carousel-item">
                            <div class="container">
                                <div class="row align-items-center">
                                    <div class="col-md-4">
                                        <div class="img-box">
                                            <img src="imagenes/alm7.jpg" alt=""/>
                                        </div>
                                    </div>

                                    <div class="col-md-8">
                                        <div class="detail-box">`
                                            <h1> <span>COMPRAS SIALSS CEDIS</span> </h1>
                                            <a href="/SIALSS_MDF/indexCompras.jsp">SIALSS COMPRAS INSUMO</a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>



                    </div>

                    <a class="carousel-control-prev" href="#carouselExampleIndicators" role="button" data-slide="prev">
                        <span class="sr-only">Previous</span>
                    </a>
                    <a class="carousel-control-next" href="#carouselExampleIndicators" role="button" data-slide="next">
                        <span class="sr-only">Next</span>
                    </a>
                </div>


            </section>
            <!-- end slider section -->
        </div>

        <script type="text/javascript" src="jsindex/jquery-3.4.1.min.js"></script>
        <script type="text/javascript" src="jsindex/bootstrap.js"></script>
        <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/OwlCarousel2/2.2.1/owl.carousel.min.js">
        </script>
        <script type="text/javascript">
            $(".owl-carousel").owlCarousel({
                loop: true,
                margin: 10,
                nav: true,
                navText: [],
                autoplay: true,
                responsive: {
                    0: {
                        items: 1
                    },
                    600: {
                        items: 2
                    },
                    1000: {
                        items: 4
                    }
                }
            });
        </script>
        <script type="text/javascript">
            $(".owl-2").owlCarousel({
                loop: true,
                margin: 10,
                nav: true,
                navText: [],
                autoplay: true,

                responsive: {
                    0: {
                        items: 1
                    },
                    600: {
                        items: 2
                    },
                    1000: {
                        items: 4
                    }
                }
            });
        </script>

    </body>
</html>