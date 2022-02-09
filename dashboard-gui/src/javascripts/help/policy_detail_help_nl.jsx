import React from 'react'
import I18n from 'i18n-js'

class PolicyDetailHelpNl extends React.Component {
  render() {
    return (
      <div className="form-element about">
        <h1>Hoe maak je autorisatieregels?</h1>

        <p>
          Autorisatieregels definiëren of een gebruiker toegang heeft tot een bepaalde dienst. De keuze wordt gemaakt op
          basis van de attributen die zijn vrijgegeven door de instelling die de gebruiker heeft
          geauthenticeerd.
        </p>

        <h2>Toegang</h2>

        <p>Wij raden aan om voor een 'Permit' regel te kiezen in plaats van een 'Deny'.</p>

        <p>
          Het algoritme dat gebruikt wordt om te bepalen of iemand toegang heeft tot een dienst op basis van een
          autorisatieregel is 'first-applicable'. Dit betekent dat de eerste match van een regel het resultaat bepaalt: 'Deny'
          of 'Permit'.
        </p>

        <p>
          Meer informatie over de implicaties van een 'Deny' regel kan{' '}
          <a
            target="_blank"
            rel="noopener noreferrer"
            href="http://docs.oasis-open.org/xacml/3.0/xacml-3.0-core-spec-os-en.html#_Toc325047268"
          >
            hier
          </a>{' '}
          worden gevonden.
        </p>

        <h2>Instelling</h2>

        <p>
          De instelling bepaalt voor welke instelling deze autorisatieregel van toepassing is. De instelling in deze
          context is de 'Identity Provider' die de gebruiker heeft geauthenticeerd. Je kan 0 of meer instellingen
          koppelen aan een autorisatieregel.
        </p>
        <br />
        <p>
          Als je het veld Instelling(en) leeg laat (je koppelt dus 0 instellingen), dan kun je alleen een
          autorisatieregel maken voor diensten waar jouw instelling eigenaar van is. De regel geldt dan voor alle
          instellingen die gebruik maken van die dienst. Als je 1 of meer instellingen selecteert dan kun je een
          autorisatieregel maken voor alle diensten die de geselecteerde instelling(en) afnemen.
        </p>

        <h2>Dienst</h2>

        <p>
          De dienst bepaalt voor welke dienst deze autorisatieregel van toepassing is. Je kunt precies 1 dienst koppelen
          aan een autorisatieregel.
        </p>

        <h2>Regel</h2>

        <p>
          Kies of dat alle attributen een match moeten opleveren of dat een enkele match voldoende is om de gebruiker te
          autoriseren voor de dienst.
        </p>

        <h2>Attributen</h2>

        <p>
          De attributen en de respectievelijke waardes bepalen of een gebruiker succesvol wordt geautoriseerd voor de
          dienst. De attributen worden gematched tegen de attributen van de gebruiker. Meer informatie over attributen
          kan worden gevonden op de{' '}
          <a target="_blank" rel="noopener noreferrer" href={I18n.t('policy_attributes.help_link')}>
            SURF wiki
          </a>
          .
        </p>

        <h2>Group naam autorisatie</h2>

        <p>
          Let op als je het attribuut <em>urn:collab:group:surfteams.nl</em> kiest. De waarde(s) van dit attribuut
          moeten de geldige en volledige ID zijn van een groep zoals weergegeven in SURFconext Teams.
        </p>
      </div>
    )
  }
}

export default PolicyDetailHelpNl
