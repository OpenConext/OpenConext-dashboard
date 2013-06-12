[#ftl]
[#--
  Copyright 2012 SURFnet bv, The Netherlands

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
  --]
[#import "macros_plaintextmail.ftl" as macros_plaintextmail/]
[#--
Template variables:
Service service
Personal-note recommendPersonalNote
String invitername
String baseURL
 --]

[@macros_plaintextmail.mailheader/]

I would like to share the following service on SURFconext with you: *${service.name?html}*.

[#if recommendPersonalNote?has_content]
  [#assign msg]${recommendPersonalNote?html}[/#assign]
  *Personal message from ${invitername?html}:* "${recommendPersonalNote?html}"
[/#if]

See here for the service details: ${appstoreURL}
Bekijk hier de service details: ${appstoreURL}

[#if service.appUrl?has_content]
Go directly to the app: ${service.appUrl}
Ga direct naar de app: ${service.appUrl}
[/#if]

[@macros_plaintextmail.mailfooter/]