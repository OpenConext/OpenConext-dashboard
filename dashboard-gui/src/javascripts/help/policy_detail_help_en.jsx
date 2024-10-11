import React from 'react'
import I18n from 'i18n-js'

class PolicyDetailHelpEn extends React.Component {
  render() {
    return (
      <div className="form-element about">
        <h1>How to create Policies?</h1>

        <p>
          Access policies define when a user is allowed to log in to an application based on the user attributes provided by
          the Institution and other attribute providers.
        </p>

        <h2>Access</h2>

        <p>We strongly recommend choosing the Permit access over the Deny access.</p>

        <p>
          The algorithm we use to determine if someone is allowed access an application based on the rules in the policy is
          'first-applicable'. This means that the first rule which holds true is the rule determining the result - i.e.
          Deny or Permit.
        </p>

        <p>
          More information about the implications of a Deny access can be found{' '}
          <a
            target="_blank"
            rel="noopener noreferrer"
            href="http://docs.oasis-open.org/xacml/3.0/xacml-3.0-core-spec-os-en.html#_Toc325047268"
          >
            here
          </a>
          .
        </p>

        <h2>Institution</h2>

        <p>
          The Institution determines for which Institution this policy applies. The Institution in this context is the
          Identity Provider which authenticated the user. You can link policies to zero or more Institutions
        </p>

        <br />
        <p>
          If you keep the Institution field empty (you select zero Institutions), you can only make authorization
          policies for application Providers owned by your Institution. The policy will apply to all Institutions connected
          to that application. If you select 1 or more Insitutions, your authorization policy can be applied to any application
          Provider those Institutions are connected to.
        </p>

        <h2>Application</h2>

        <p>The application determines for which application this policy applies. You can only link policies to one application.</p>

        <h2>Rule</h2>

        <p>
          Choose if all the attributes defined in this policy must match the attributes of the user or that one match is
          sufficient for a 'Permit'
        </p>

        <h2>Attributes</h2>

        <p>
          The attributes and their values actually define if a user is granted access to the application or not. For more
          information about attributes see the{' '}
          <a target="_blank" rel="noopener noreferrer" href={I18n.t('policy_attributes.help_link')}>
            SURF wiki
          </a>
          .
        </p>

        <h2>Group name authorization</h2>

        <p>
          Special care must be taken when you choose <em>urn:collab:group:surfteams.nl</em> as a required attribute. The
          value must be the fully qualified group name where the user is a member of, as displayed in SURFconext Teams.
        </p>
      </div>
    )
  }
}

export default PolicyDetailHelpEn
