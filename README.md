<p align="center"><a href="https://github.com/TayfunCesur/CurvedBottomSheet" target="_blank"><img width="150"src="art/dolphin.png"></a></p>
<h1 align="center">Curved Bottom Sheet </h1>
<p align="center">Curved-Waved-Animated BottomSheet 😎</p>
<p align="center">
  <a href="https://github.com/TayfunCesur/CurvedBottomSheet"><img src="https://badges.frapsoft.com/os/v1/open-source.svg?v=103" ></a>
  <a href="https://circleci.com/gh/TayfunCesur/CurvedBottomSheet"><img src="https://circleci.com/gh/TayfunCesur/CurvedBottomSheet.svg?style=svg" alt="Build Status"></a>
    <a href="https://android-arsenal.com/api?level=16"><img src="https://img.shields.io/badge/API-16%2B-orange.svg?style=flat" alt="api"></a>
    <a href="https://jitpack.io/#TayfunCesur/CurvedBottomSheet"><img src="https://jitpack.io/v/TayfunCesur/CurvedBottomSheet.svg" alt="jitpack"></a>
   <a href="https://android-arsenal.com/details/1/7716"><img src="https://img.shields.io/badge/Android%20Arsenal-Curved%20Bottom%20Sheet-green.svg?style=flat" alt="jitpack"></a>
  
</p>


### Outputs
Curved-Bottom-Concave             |  Curved-Bottom-Convex             |  Curved-Top-Concave             |  Curved-Top-Convex             |  Waved
:-------------------------:|:-------------------------:|:-------------------------:|:-------------------------:|:-------------------------:
<img height="300" src="/art/bottomAndConcave.gif"></img>  |  <img height="300" src="/art/bottomAndConvex.gif"></img>  |  <img height="300" src="/art/topAndConcave.gif"></img>  |  <img height="300" src="/art/topAndConvex.gif"></img>  |  <img height="300" src="/art/waved.gif"></img>

### Summary
Curved Bottom Sheet, helps you to display fancy, extraordinary UX with Bottom Sheet Behavior. Even if its name contains Bottom,it also supports Top Sheet.If you bored classic BottomSheet, go on.

### Download

This library is available in **jitpack**, so you need to add this repository to your root build.gradle at the end of repositories:
   
```groovy  
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```
Add the dependency:
    <a href="https://jitpack.io/#TayfunCesur/CurvedBottomSheet"><img src="https://jitpack.io/v/TayfunCesur/CurvedBottomSheet.svg" alt="jitpack"></a>
    
```groovy 
dependencies {
    implementation "com.github.TayfunCesur:CurvedBottomSheet:$latest_version"
}
``` 
**Note:** This project has been migrated to AndroidX at v1.1 by the contribution of @ChintanRathod.
If you haven't migrated your project to AndroidX yet, you can use v1.0.2

```groovy 
dependencies {
    implementation "com.github.TayfunCesur:CurvedBottomSheet:1.0.2"
}
```

## Sample Usage  

The sample app has all but if you want quick tip,


```
    <android.support.design.widget.CoordinatorLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent">

        <com.tayfuncesur.curvedbottomsheet.CurvedLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:id="@+id/bottom_sheet"
                app:fillColor="@color/colorAccent"
                app:behavior_hideable="false"
                app:behavior_peekHeight="200dp"
                app:layout_behavior="android.support.design.widget.BottomSheetBehavior">
                
                //Your view here
                
     </com.tayfuncesur.curvedbottomsheet.CurvedLayout>
 </android.support.design.widget.CoordinatorLayout>

```

In your activity or fragment, 
```
CurvedBottomSheet(view = bottom_sheet,
shape = CurvedBottomSheet.Shape.Concave,
location = CurvedBottomSheet.Location.BOTTOM,
type = CurvedBottomSheet.Type.CURVE)
.init()
```

### Parameters
Parameter | Type | Description
--- | --- | ---
radius | float | The default value is 180F. This parameter is so important to your curves beauty. In general, you must pass this parameter as your **(screen width / 6)**. If you curios about reason, don't forget to have look at the bottom of page.
shape | Enum(Concave,Convex) | The default value is Concave. This parameter helps you to decide your shape is convex or concave.
location | Enum(TOP,BOTTOM) | The default value is BOTTOM.If you want to use TOP, **don't forget to pass app:layout_behavior="com.tayfuncesur.curvedbottomsheet.TopSheetBehavior" to your CurvedLayout.**
type | Enum(CURVE,WAVE) | The default value is CURVE. This parameter draws a wave or curve.
callback | null | The default value is null. This parameter allows you notified while sheet is scrolling.Maybe you want to do some magic works while scrolling. **For instance, alpha animation. (It is done in the WavedActivity)**


### Attributes
Property | Type | Description
--- | --- | ---
fillColor | color | The default color is White. This parameter fills your Curved layout background. **Don't use backgroundColor property because you don't want to fill whole layout that includes the places outside of curve or wave**
showControlPoints | boolean | The default value is false. This parameters draws points in layout. If you work debug mode,it can help you to determine the your control points. 


### Clarification
Explanation of app:fillColor             |  Explanation of app:showControlPoints           
:-------------------------:|:-------------------------:
<img height="400" src="/art/fillColor.png"></img>  |  <img height="400" src="/art/controlexpl.png"></img>

### Bonus Part : Alpha Animation

This bonus part has already implemented in [WavedActivity](https://github.com/TayfunCesur/CurvedBottomSheet/blob/master/app/src/main/java/com/tayfuncesur/curvedbottomsheetdemo/WavedActivity.kt)

If you pass the [Callback](https://github.com/TayfunCesur/CurvedBottomSheet/blob/master/lib/src/main/java/com/tayfuncesur/curvedbottomsheet/Callback.kt) parameter to CurvedBottomSheet, you gonna have 
```
            object : Callback {
                override fun onSlide(p0: View, p1: Float) {
                    // Here you can play alpha with p1 value. 
                    contentLayout.alpha = p1
                    dolphinLayout.alpha = 1 - p1
                }
```

### Useful Resources
 - Here is a good [article](https://ciechanow.ski/drawing-bezier-curves/)
 - [Online tool](https://www.desmos.com/calculator/cahqdxeshd) to play with it 
 - [Great Explanation Video](https://www.youtube.com/watch?v=TeXajQ62yZ8) in just 4 min

## Project Maintained By

### [Tayfun Cesur](https://twitter.com/CesurTayfun35)

Open-Source Enthusiast | Android Engineer

<a href="https://www.linkedin.com/in/tayfun-cesur-353958157/"><img src="https://seeklogo.com/images/L/linkedin-in-icon-logo-2E34704F04-seeklogo.com.png" width="40" style="margin-right:8px"></a>
<a href="https://twitter.com/CesurTayfun35"><img src="https://seeklogo.com/images/T/twitter-2012-positive-logo-916EDF1309-seeklogo.com.png" width="40" style="margin-right:8px"></a>

### Greetings
If you have any questions, hit me on [Twitter](https://twitter.com/CesurTayfun35)

## Licence
```
Copyright 2019 Tayfun CESUR

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```





