package api.task

import sttp.tapir.Tapir
import sttp.tapir.generic.auto.SchemaDerivation
import sttp.tapir.json.circe.TapirJsonCirce


trait TaskApi extends Tapir with TapirJsonCirce with SchemaDerivation{

}
