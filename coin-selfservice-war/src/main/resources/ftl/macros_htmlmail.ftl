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

[#-- Macros for HTML mails --]

[#macro mailheader]
[#-- In head: Generate more than 1109 spaces for iOS devices, but no more than 250 chars at a time --]
<html>
<head><title></title>
[#list 1..1110 as x]${' '}[#if x % 250 = 0]${'\n'}[/#if][/#list]
</head>
<body style="color:#333333;mso-line-height-rule:exactly;line-height:18px;font-size:13px;font-family:Arial, sans-serif;">
<table width="100%" border="0" cellspacing="0" cellpadding="0"
       style="color:#333333;mso-line-height-rule:exactly;line-height:18px;font-size:13px;font-family:Arial, sans-serif;">
  <tr>
    <td>
      <style type="text/css">
        .ReadMsgBody {
          width: 100%;
        }

        .ExternalClass {
          width: 100%;
        }

        body {
          color: #333333;
          line-height: 18px;
          font-size: 13px;
          font-family: Arial, sans-serif;
        }

        body, td {
          font-family: Arial, sans-serif;
          font-size: 13px;
          mso-line-height-rule: exactly;
          line-height: 18px;
          color: #333333;
        }

        h1 {
          font-size: 28px;
          font-weight: normal;
          mso-line-height-rule: exactly;
          line-height: 33px;
        }

        a, a:visited {
          color: #0088CC;
        }

        span.yshortcuts {
          color: #000;
          background-color: none;
          border: none;
        }

        span.yshortcuts:hover,
        span.yshortcuts:active,
        span.yshortcuts:focus {
          color: #000;
          background-color: none;
          border: none;
        }
      </style>
      <div
          style="max-width:960px;border-radius:4px 4px 4px 4px;margin-bottom:0 ;margin-left:auto;margin-right:auto;margin-top:0 ;padding-bottom:1%;padding-left:1%;padding-right:1%;padding-top:1%;border-style:solid;border-width:1px;border-color:#D8DADC;">
        <img src="https://static.surfconext.nl/media/surfconext.png" width="63" height="40"
            alt="SURFconext logo" align="right"/>

        <h1 style="mso-line-height-rule:exactly;line-height:33px;font-weight:normal;font-size:28px;margin-top:0;">
          SURFconext</h1>

[/#macro]


[#macro mailfooter]
          <p lang="en">
            This service is powered by <a href="http://www.surfconext.nl" style="color:#0088CC;">SURFconext</a> - brought
            to you by SURFnet
          </p>
        </div>
      </td>
    </tr>
  </table>
</body>
</html>
[/#macro]