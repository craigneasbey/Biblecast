/// <reference path="./typings/angular2/angular2.d.ts"/>

import {Component, View, bootstrap} from 'angular2/angular2';

// Annotation section
@Component({
  selector: 'my-app'
})
@View({
  template: '<h3>{{ screenSizeText }}</h3>'
})
// Component controller
class MyAppComponent {
  screenSizeText: string;
  constructor() {
    this.screenSizeText = screenSize();
  }

  function screenSize() {
    var txt = "";
    txt += "<p>Total width/height: " + screen.width + "*" + screen.height + "</p>";
    txt += "<p>Available width/height: " + screen.availWidth + "*" + screen.availHeight + "</p>";
    txt += "<p>Color depth: " + screen.colorDepth + "</p>";
    txt += "<p>Color resolution: " + screen.pixelDepth + "</p>";
    return txt;
  }
}

bootstrap(MyAppComponent);
