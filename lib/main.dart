import 'package:auto_start_flutter/auto_start_flutter.dart';
import 'package:flutter/material.dart';
import 'dart:async';
import 'package:flutter/services.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'Company Splash Screen',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: SplashScreen(),
    );
  }
}

class SplashScreen extends StatefulWidget {
  @override
  _SplashScreenState createState() => _SplashScreenState();
}

class _SplashScreenState extends State<SplashScreen> {
  @override
  void initState() {
    super.initState();
    initAutoStart();
    Timer(Duration(seconds: 60), () {
      SystemNavigator.pop();
    });
  }

  Future<void> initAutoStart() async {
    try {
      bool? test = await isAutoStartAvailable;
      print("Test: $test");
      if (test ?? false) await getAutoStartPermission();
    } on PlatformException catch (e) {
      print(e);
    }
    if (!mounted) return;
  }


  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.black,
      body: Container(
        width: double.infinity,
        height: MediaQuery.of(context).size.height,
        child: Center(
          child: Image.asset("assets/logo.jpg"),
        ),
      ),
    );
  }
}


const platform = MethodChannel('com.example.companyoverlay/overlay');

void requestOverlayPermission() async {
  try {
    await platform.invokeMethod('requestOverlayPermission');
  } on PlatformException catch (e) {
    print("Failed to request overlay permission: '${e.message}'.");
  }
}