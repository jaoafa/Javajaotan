<?php
date_default_timezone_set("Asia/Tokyo");
define("Version", date("Y.m.d_H.i")); // バージョン
define("FileLocation", __DIR__ . "/src/main/resources/version");

if (file_exists(dirname(FileLocation))){
    mkdir(dirname(FileLocation), 0777, true);
}

file_put_contents(FileLocation, Version);
