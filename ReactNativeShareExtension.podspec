Pod::Spec.new do |s|
    s.name         = "ReactNativeShareExtension"
    s.version      = "1.1.2"
    s.summary      = "Share Extension for React Native"
  
    s.homepage     = "https://github.com/cjanietz/react-native-share-extension"
  
    s.license      = "MIT"
    s.authors      = "Christopher Janietz"
    s.platform     = :ios, "9.0"
  
    s.source       = { :git => "https://github.com/cjanietz/react-native-share-extension.git" }
  
    s.source_files  = "ios/*.{h,m}"
  
    s.dependency 'React'
  end
  