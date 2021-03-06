package org.osm2world.core.map_elevation.creation;

import java.util.List;

import org.osm2world.core.heightmap.data.CellularTerrainElevation;
import org.osm2world.core.heightmap.data.TerrainPoint;
import org.osm2world.core.map_data.data.MapArea;
import org.osm2world.core.map_data.data.MapData;
import org.osm2world.core.map_data.data.MapNode;
import org.osm2world.core.map_data.data.MapWaySegment;
import org.osm2world.core.map_elevation.data.AreaElevationProfile;
import org.osm2world.core.map_elevation.data.NodeElevationProfile;
import org.osm2world.core.map_elevation.data.WaySegmentElevationProfile;

/**
 * assigns an elevation of 0 to everything.
 * Useful for certain use cases, e.g. fast creation of tiled pseudo-3D tiles.
 */
public class ZeroElevationCalculator implements ElevationCalculator {

	@Override
	public void calculateElevations(MapData mapData,
			CellularTerrainElevation eleData) {
				
		for (MapNode node : mapData.getMapNodes()) {
							
			NodeElevationProfile profile = new NodeElevationProfile(node);
			profile.setEle(0);
			node.setElevationProfile(profile);
						
		}
		
		for (MapWaySegment segment : mapData.getMapWaySegments()) {

			if (segment.getPrimaryRepresentation() == null) continue;
			
			WaySegmentElevationProfile profile =
				new WaySegmentElevationProfile(segment);
			
			profile.addPointWithEle(
				segment.getStartNode().getElevationProfile().getPointWithEle());
			profile.addPointWithEle(
				segment.getEndNode().getElevationProfile().getPointWithEle());
			
			segment.setElevationProfile(profile);
			
		}
		
		/* set areas' elevation profiles (based on nodes' elevations) */
		
		for (MapArea area : mapData.getMapAreas()) {
			
			if (area.getPrimaryRepresentation() == null) continue;
			
			AreaElevationProfile profile =
				new AreaElevationProfile(area);
			
			for (MapNode node : area.getBoundaryNodes()) {
				profile.addPointWithEle(
					node.getElevationProfile().getPointWithEle());
			}
			
			for (List<MapNode> holeOutline : area.getHoles()) {
				for (MapNode node : holeOutline) {
					profile.addPointWithEle(
						node.getElevationProfile().getPointWithEle());
				}
			}
			
			area.setElevationProfile(profile);
			
		}
		
		if (eleData != null) {
			for (TerrainPoint point : eleData.getTerrainPoints()) {
				point.setEle(0);
			}
		}
		
	}
	
}
