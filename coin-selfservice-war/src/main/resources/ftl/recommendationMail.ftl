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

[#import "macros_htmlmail.ftl" as macros_htmlmail/]
[#--
Template variables:
CompoundServiceprovider compoundSp
Personal-note recommendPersonalNote
String invitername
String appstoreURL
 --]
[@macros_htmlmail.mailheader/]
        <p lang="en">
          I would like to share the following service on SURFconext with you:
          ${compoundSp.sp.name?html}
        </p>

        [#if recommendPersonalNote?has_content]
        <p>
          [#assign msg]${recommendPersonalNote?html}[/#assign]
          <strong>Personal message from ${invitername?html}:</strong><br /> "${msg?replace("\n","<br />")}"
        </p>
        [/#if]

        <table cellpadding="10" width="90%" align="center" style="margin-bottom:1em;margin-left:auto;margin-right:auto;margin-top:1em;">
          <tr>
            <td bgcolor="#EDFFDE" style="mso-line-height-rule:exactly;line-height:18px;font-size:13px;font-family:Arial, sans-serif;border-radius:4px 4px 4px 4px;color:#489406;border-style:solid;border-width:1px;border-color:#489406;"
                align="center" width="50%">
                <span lang="en"><a href="${appstoreURL}" style="color:#0088CC;">Click here to see the service details</a></span>
                <br/><span lang="nl"><a href="${appstoreURL}" style="color:#0088CC;">Klik hier voor details van de dienst</a></span>
            </td>
            [#if compoundSp.appUrl?has_content]
              <td bgcolor="#EDFFDE" style="mso-line-height-rule:exactly;line-height:18px;font-size:13px;font-family:Arial, sans-serif;border-radius:4px 4px 4px 4px;color:#489406;border-style:solid;border-width:1px;border-color:#489406;"
                  align="center" width="50%">
                  <span lang="en"><a href="${compoundSp.appUrl}" style="color:#0088CC;">Go directly to the app</a></span>
                  <br/><span lang="nl"><a href="${compoundSp.appUrl}" style="color:#0088CC;">Ga direct naar de app</a></span>
              </td>
            [#else]
              <td></td>
            [/#if]
          </tr>
        </table>
        <p lang="en">
          Kind regards,<br/>
          ${invitername?html}
        </p>
[@macros_htmlmail.mailfooter/]