<?php
date_default_timezone_set("Asia/Tokyo");
define("Version", date("Y.m.d_H.i")); // バージョン
define("FileLocation", __DIR__ . "/src/main/resources/version");

file_put_contents(FileLocation, Version);
