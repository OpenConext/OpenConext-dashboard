import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faQuestionCircle} from "@fortawesome/free-regular-svg-icons";
import ReactTooltip from "react-tooltip";

export default function Tooltip({ id, text }) {
    return (
        <div>
            <FontAwesomeIcon icon={faQuestionCircle} data-tip data-for={id} />
            <ReactTooltip id={id} type="info" class="tool-tip" effect="solid" multiline delayHide={250} clickable>
                <span dangerouslySetInnerHTML={{ __html: text }} />
            </ReactTooltip>
        </div>
    )
}

