
Pod::Spec.new do |s|
  s.name         = "RNReactNativePalmmob"
  s.version      = "1.0.0"
  s.summary      = "RNReactNativePalmmob"
  s.description  = <<-DESC
                  RNReactNativePalmmob
                   DESC
  s.homepage     = "http://www.palmmob.com/"
  s.license      = "MIT"
  # s.license      = { :type => "MIT", :file => "FILE_LICENSE" }
  s.author             = { "author" => "author@domain.cn" }
  s.platform     = :ios, "7.0"
  s.source       = { :git => "https://github.com/author/RNReactNativePalmmob.git", :tag => "master" }
  s.source_files  = "RNReactNativePalmmob/**/*.{h,m}"
  s.requires_arc = true


  s.dependency "React"
  #s.dependency "others"

end

  